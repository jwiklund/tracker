package source

import (
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"
)

func NewSimple(title, url, urlPattern string, patterns StringMap) Source {
	return &simple{title, url, urlPattern, patterns}
}

type simple struct {
	title      string
	url        string
	urlPattern string
	patterns   StringMap
}

func (s *simple) Title() string {
	return s.title
}

func (s *simple) Url() string {
	return s.url
}

func (s *simple) Items() ([]Item, error) {
	resp, err := http.Get(s.url)
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", s.url, err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	return parseSimpleItems(resp.Body, s.urlPattern, s.patterns)
}

func parseSimpleItems(source io.Reader, urlPattern string, patterns StringMap) ([]Item, error) {
	res, err := ParseSimple(source, patterns)
	if err != nil {
		return nil, err
	}
	if len(res["curr"]) != 1 {
		return nil, errors.New(fmt.Sprintf("invalid simple page, got no current link (len == %d)", len(res["curr"])))
	}
	item := func(title, id string, day int) Item {
		d := time.Now().AddDate(0, 0, day)
		date := d.Format("2006-01-02") + " 01:01:01"
		url := fmt.Sprintf(urlPattern, id)
		return Item{title, url, url, date, fmt.Sprintf("<img src=\"%s\" title=\"%s\">", url, title)}
	}
	id := res["curr"][0]
	title := id
	if len(res["title"]) > 0 {
		title = res["title"][0]
	}
	r := []Item{item(title, id, 0)}
	if len(res["prev"]) > 0 {
		id = res["prev"][0]
		r = append(r, item(id, id, -1))
	}
	return r, nil
}
