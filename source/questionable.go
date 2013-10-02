package source

import (
	"errors"
	"fmt"
	"io"
	"net/http"
)

func NewQuestionable() Source {
	return &questionable{"http://questionablecontent.net/"}
}

type questionable struct {
	url string
}

func (q questionable) Items() ([]string, error) {
	resp, err := http.Get(q.url)
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", q.url, err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	return parseQuestionable(resp.Body)
}

func parseQuestionable(source io.Reader) ([]string, error) {
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
	r := []string{}
	if len(res["prev"]) > 0 {
		r = append(r, fmt.Sprintf("http://www.questionablecontent.net/comics/%s.png", res["prev"][0]))
	}
	r = append(r, fmt.Sprintf("http://www.questionablecontent.net/comics/%s.png", res["curr"][0]))
	return r, nil
}
