package source

import (
	"code.google.com/p/go-html-transform/h5"
	"code.google.com/p/go.net/html"
	"code.google.com/p/go.net/html/atom"
	"errors"
	"fmt"
	"io"
	"regexp"
	"strings"
)

type HorribleItem struct {
	Name     string
	Episode  string
	Torrents map[string]string
}

func (ht *HorribleItem) String() string {
	return fmt.Sprintf("%s - %s at %d locations", ht.Name, ht.Episode, len(ht.Torrents))
}

type Html struct {
	Node *html.Node
}

func (h Html) EachChild(f func(*html.Node) bool) {
	child := h.Node.FirstChild
	if child == nil {
		return
	}
	for {
		if !f(child) {
			return
		}
		if child == h.Node.LastChild {
			return
		}
		child = child.NextSibling
	}
}

func (h Html) Attribute(name string) string {
	for _, a := range h.Node.Attr {
		if a.Key == name {
			return a.Val
		}
	}
	return ""
}

func (h Html) Find(f func(h Html) bool) (Html, error) {
	if f(h) {
		return h, nil
	}
	children := h.Children(atom.Atom(0))
	for _, c := range children {
		if f(c) {
			return c, nil
		}
		r, e := c.Find(f)
		if e == nil {
			return r, nil
		}
	}
	return Html{}, errors.New("Not found")
}

func (h Html) FindAll(f func(h Html) bool) []Html {
	children := h.Children(atom.Atom(0))
	var res []Html
	for _, c := range children {
		if f(c) {
			res = append(res, c)
		} else {
			res = append(res, c.FindAll(f)...)
		}
	}
	return res
}

func (h Html) Child(childType atom.Atom) Html {
	var child Html
	h.EachChild(func(n *html.Node) bool {
		if n.DataAtom == childType {
			child = Html{n}
			return false
		}
		return true
	})
	return child
}

func (h Html) Children(childType atom.Atom) []Html {
	var res []Html
	h.EachChild(func(n *html.Node) bool {
		if n.DataAtom == childType || int(childType) == 0 {
			res = append(res, Html{n})
		}
		return true
	})
	return res
}

func (h Html) ChildAtPath(childTypes ...atom.Atom) (Html, error) {
	path := []string{}
	node := h
	for _, childType := range childTypes {
		next := node.Child(childType)
		path = append(path, childType.String())
		if next.Node == nil {
			children := HtmlArrayToString(node.Children(atom.Atom(0)), HtmlToNodeType)
			return Html{}, errors.New(fmt.Sprintf("Path %s, could not find %s in %s",
				strings.Join(path, "/"), childType.String(), strings.Join(children, ",")))
		}
		node = next
	}
	return node, nil
}

func (h Html) Text() string {
	var res string
	h.EachChild(func(n *html.Node) bool {
		if n.Type == html.TextNode {
			res = res + n.Data
		}
		return true
	})
	return res
}

func (h Html) String() string {
	return h5.NewTree(h.Node).String()
}

func HtmlArrayToString(h []Html, f func(h Html) string) []string {
	res := make([]string, len(h))
	for i, item := range h {
		res[i] = f(item)
	}
	return res
}

func HtmlToNodeType(h Html) string {
	return h.Node.DataAtom.String()
}

func parseLatestHorrible(source io.Reader) ([]HorribleItem, error) {
	tree, err := h5.New(source)
	if err != nil {
		return nil, err
	}
	top := Html{tree.Top()}
	body, err := top.ChildAtPath(atom.Html, atom.Body)
	if err != nil {
		return nil, err
	}
	var rs []HorribleItem
	for _, episode := range body.Children(atom.Div) {
		item, err := parseLatestHorribleItem(episode)
		if err != nil {
			return nil, errors.New(err.Error() + " parsing " + episode.String())
		}
		rs = append(rs, *item)
	}
	return rs, nil
}

func parseLatestHorribleItem(episode Html) (*HorribleItem, error) {
	episode_pattern := regexp.MustCompile("\\([0-9/]+\\) (.*) - (\\d+)")
	episode_match := episode_pattern.FindAllStringSubmatch(episode.Text(), -1)
	if len(episode_match) == 0 {
		return nil, errors.New("Could not parse title/episode " + episode.Text())
	}
	var res HorribleItem
	res.Name = strings.Trim(episode_match[0][1], " ")
	res.Episode = strings.Trim(episode_match[0][2], " ")
	res.Torrents = make(map[string]string)
	links := episode.FindAll(func(h Html) bool {
		return h.Node.DataAtom == atom.Span && h.Attribute("class") == "resolution-links"
	})
	for _, link := range links {
		format, link, linkType, err := parseLatestHorribleLink(link)
		if err != nil {
			return nil, err
		}
		if linkType == "Torrent" {
			res.Torrents[format] = link
		}
	}
	if len(res.Torrents) == 0 {
		return nil, errors.New("No links found in " + episode.String())
	}
	return &res, nil
}

func parseLatestHorribleLink(link Html) (string, string, string, error) {
	span, err := link.Find(func(h Html) bool {
		if h.Node.DataAtom == atom.Span && h.Attribute("class") == "resolution-links" {
			return true
		}
		return false
	})
	if err != nil {
		return "", "", "", errors.New("Could not find span[@class='resolution-links'] in " + link.String())
	}
	a, err := span.Find(func(h Html) bool {
		return h.Node.DataAtom == atom.A
	})
	if err != nil {
		return "", "", "", errors.New("Could not find link in " + span.String())
	}
	return span.Attribute("id"), a.Attribute("href"), a.Text(), nil
}
