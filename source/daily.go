package source

import (
	"code.google.com/p/go.net/html/atom"
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"
)

func NewDailyShow() Source {
	return &dailyShow{}
}

type dailyShow struct{}

func (ds *dailyShow) Title() string {
	return "The Daily Show"
}

func (ds *dailyShow) Url() string {
	return "http://www.thedailyshow.com/full-episodes/"
}

func (ds *dailyShow) Items() ([]Item, error) {
	resp, err := http.Get(ds.Url())
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", ds.Url(), err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	return parseDailyShow(resp.Body)
}

func parseDailyShow(body io.Reader) ([]Item, error) {
	top, err := NewHtml(body)
	if err != nil {
		return nil, err
	}
	epDiv, err := top.Find(func(h Html) bool {
		return "module tds_full_eps_m03" == h.Attribute("class")
	})
	if err != nil {
		return nil, errors.New("Could not find episodes div [@class='module tds_full_eps_m03']")
	}
	episodes := epDiv.FindAll(func(h Html) bool {
		return h.Node.DataAtom == atom.Li
	})
	var res []Item
	for _, episode := range episodes {
		item, err := parseDailyShowItem(episode)
		if err != nil && err.Error() != "Not a link" {
			return nil, err
		}
		res = append(res, item)
	}
	return res, nil
}

func parseDailyShowItem(li Html) (Item, error) {
	a := li.Child(atom.A)
	if a.Node == nil {
		return Item{}, errors.New("Not a link")
	}
	link := a.Attribute("href")
	details, err := a.Find(func(h Html) bool {
		return h.Attribute("class") == "details"
	})
	if err != nil {
		return Item{}, errors.New("No details element in " + a.String())
	}
	detailspan := details.Child(atom.Span)
	if detailspan.Node == nil {
		return Item{}, errors.New("No inner span in " + details.String())
	}
	airdate, err := a.Find(func(h Html) bool {
		return h.Attribute("class") == "air_date"
	})
	var date string
	if err != nil {
		date = "unknown"
	} else {
		date, err = parseDailyShowDate(airdate)
		if err != nil {
			return Item{}, err
		}
	}
	title := detailspan.Text()
	content := fmt.Sprintf("<a href='%s'>%s</a>", link, title)
	return Item{title, link, link, date, content}, nil
}

func parseDailyShowDate(air Html) (string, error) {
	span := air.Child(atom.Span)
	if span.Node == nil {
		return "", errors.New("No date span in " + air.String())
	}
	var all = span.Text() + air.Text()
	airtime, err := time.Parse("Mon Jan 2", all)
	if err != nil {
		return "", err
	}
	withyear := time.Date(time.Now().Year(), airtime.Month(), airtime.Day(), 0, 0, 0, 0, time.UTC)
	return withyear.Format("2006-01-02") + " 01:01:01", nil
}
