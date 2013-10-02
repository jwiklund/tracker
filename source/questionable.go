package source

import (
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"
)

func NewQuestionable() Source {
	return &questionable{"http://questionablecontent.net/"}
}

type questionable struct {
	url string
}

func (q *questionable) Title() string {
	return "Questionable Content"
}

func (q *questionable) Url() string {
	return q.url
}

func (q questionable) Items() ([]Item, error) {
	resp, err := http.Get(q.url)
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", q.url, err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	return parseQuestionable(resp.Body)
}

func parseQuestionable(source io.Reader) ([]Item, error) {
	patterns := map[string]string{
		"prev": "<li><a href=\"view.php\\?comic=(\\d+)\">Previous</a></li>",
		"curr": "<img id=\"strip\" src=\"http://www.questionablecontent.net/comics/(\\d+).png\">",
	}
	res, err := parseSource(patterns, source)
	if err != nil {
		return nil, err
	}
	if len(res["curr"]) != 1 {
		return nil, errors.New(fmt.Sprintf("invalid questionable page, got no current link (len == %d)", len(res["cur"])))
	}
	item := func(id string, day int) Item {
		d := time.Now().AddDate(0, 0, day)
		date := d.Format("2006-01-02") + " 01:01:01"
		url := fmt.Sprintf("http://www.questionablecontent.net/comics/%s.png", id)
		return Item{id, url, url, date, fmt.Sprintf("<img src=\"%s\">", url)}
	}
	r := []Item{item(res["curr"][0], 0)}
	if len(res["prev"]) > 0 {
		r = append(r, item(res["prev"][0], -1))
	}
	return r, nil
}
