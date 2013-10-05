package source

import (
	"code.google.com/p/go-html-transform/h5"
	"code.google.com/p/go.net/html"
	"code.google.com/p/go.net/html/atom"
	"errors"
	"fmt"
	"io"
	"regexp"
	"strconv"
	"strings"
)

type HorribleItem struct {
	Name     string
	Episode  string
	Torrents map[string]string
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

func (h Html) GetChild(childType atom.Atom) Html {
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

func (h Html) GetChildren(childType atom.Atom) []Html {
	var res []Html
	h.EachChild(func(n *html.Node) bool {
		if n.DataAtom == childType || int(childType) == 0 {
			res = append(res, Html{n})
		}
		return true
	})
	return res
}

func (h Html) GetChildPath(childTypes ...atom.Atom) (Html, error) {
	path := []string{}
	node := h
	for _, childType := range childTypes {
		next := node.GetChild(childType)
		path = append(path, childType.String())
		if next.Node == nil {
			children := HtmlArrayToString(node.GetChildren(atom.Atom(0)), HtmlToNodeType)
			return Html{}, errors.New(fmt.Sprintf("Path %s, could not find %s in %s",
				strings.Join(path, "/"), childType.String(), strings.Join(children, ",")))
		}
		node = next
	}
	return node, nil
}

func (h Html) GetText() string {
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
	body, err := top.GetChildPath(atom.Html, atom.Body)
	if err != nil {
		return nil, err
	}
	var rs []HorribleItem
	for _, episode := range body.GetChildren(atom.Div) {
		item, err := parseLatestHorribleItem(episode)
		if err != nil {
			return nil, errors.New(err.Error() + " parsing " + episode.String())
		}
		rs = append(rs, *item)
	}
	return rs, nil
}

func parseLatestHorribleItem(episode Html) (*HorribleItem, error) {
	name_pat := regexp.MustCompile("\\([0-9/]+\\) (.*) - \\d+")
	children := HtmlArrayToString(episode.GetChildren(atom.Atom(0)), func(h Html) string {
		return h.Node.DataAtom.String() + "==" + strconv.Itoa(int(h.Node.DataAtom)) + ":" + h.String()
	})
	fmt.Printf(strings.Join(children, ", ") + "\n")
	fmt.Println(episode.GetText())
	name_pat.String()
	return nil, errors.New("Not implemented")
}
