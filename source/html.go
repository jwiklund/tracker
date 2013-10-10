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

func NewHtml(source io.Reader) (Html, error) {
	tree, err := h5.New(source)
	if err != nil {
		return Html{}, err
	}
	return Html{tree.Top()}, nil
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
