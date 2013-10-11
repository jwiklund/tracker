package source

import (
	"errors"
	"fmt"
	"github.com/PuerkitoBio/goquery"
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

	doc, err := goquery.NewDocumentFromReader(body)
	if err != nil {
		return nil, err
	}
	var res []Item
	doc.Find("div.tds_full_eps_m03 li a").Each(func(i int, s *goquery.Selection) {
		href, found := s.Attr("href")
		if !found {
			return
		}
		details := s.Find(".details").Text()
		air_date := s.Find(".air_date").Text()
		date, err := time.Parse("Mon Jan  2", air_date)
		if err == nil {
			withyear := time.Date(time.Now().Year(), date.Month(), date.Day(), 0, 0, 0, 0, time.UTC)
			air_date = withyear.Format("2006-01-02") + " 01:01:01"
		}
		guest := s.Find(".guest").Text()
		if guest[len(guest)-1] == '-' {
			guest = guest[0 : len(guest)-3]
		}
		title := air_date + " " + guest
		content := fmt.Sprintf("<a href='%s'>%s</a>", href, details)
		res = append(res, Item{title, href, href, air_date, content})
	})
	if len(res) == 0 {
		return nil, errors.New("No episodes div [@class tds_full_eps_m03] found in body")
	}
	return res, nil
}
