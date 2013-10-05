package source

import (
	"code.google.com/p/go-html-transform/h5"
	"code.google.com/p/go.net/html"
	"code.google.com/p/go.net/html/atom"
	"errors"
	"fmt"
	"io"
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

func (h Html) GetChild(childType atom.Atom) Html {
	child := h.Node.FirstChild
	if child == nil {
		return Html{}
	}
	for {
		if child.DataAtom == childType {
			return Html{child}
		}
		if child == h.Node.LastChild {
			return Html{nil}
		}
		child = child.NextSibling
	}
}

func (h Html) GetChildren(childType atom.Atom) []Html {
	var res []Html
	child := h.Node.FirstChild
	if child == nil {
		return res
	}
	for {
		if child.DataAtom == childType || int(childType) == 0 {
			res = append(res, Html{child})
		}
		if child == h.Node.LastChild {
			return res
		}
		child = child.NextSibling
	}
}

func (h Html) GetChildPath(childTypes ...atom.Atom) (Html, error) {
	path := []string{}
	node := h
	for _, childType := range childTypes {
		next := node.GetChild(childType)
		path = append(path, childType.String())
		if next.Node == nil {
			childs := node.GetChildren(atom.Atom(0))
			var children []string
			for _, child := range childs {
				children = append(children, child.Node.DataAtom.String())
			}
			return Html{}, errors.New(fmt.Sprintf("Path %s, could not find %s in %s",
				strings.Join(path, "/"), childType.String(), strings.Join(children, ",")))
		}
		node = next
	}
	return node, nil
}

func (h Html) String() string {
	return h5.NewTree(h.Node).String()
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
	return nil, errors.New("Not implemented")
}
