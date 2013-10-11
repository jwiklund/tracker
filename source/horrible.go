package source

import (
	"code.google.com/p/go.net/html"
	"errors"
	"fmt"
	"github.com/PuerkitoBio/goquery"
	"io"
	"log"
	"net/http"
	"regexp"
	"strings"
	"time"
)

func NewHorrible() Source {
	return &horrible{}
}

type horrible struct {
}

func (h *horrible) Title() string {
	return "Horrible Subs"
}

func (h *horrible) Url() string {
	return "http://horriblesubs.info/lib/latest.php"
}

func (h *horrible) Items() ([]Item, error) {
	resp, err := http.Get(h.Url())
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", h.Url(), err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	items, err := parseLatestHorrible(resp.Body)
	if err != nil {
		return nil, err
	}
	res := make([]Item, len(items))
	for i, item := range items {
		var it Item
		// TODO parse and store
		it.Date = time.Now().Format("2006-01-02") + " 01:01:01"
		link := item.Torrents["720p"]
		if link == "" {
			for _, k := range item.Torrents {
				link = item.Torrents[k]
				break
			}
		}
		it.GUID = link
		it.Link = link
		it.Title = item.Name + " - " + item.Episode
		// TODO anidb link
		it.Content = fmt.Sprintf("<a href='%s'>%s</a>", link, it.Title)
		res[i] = it
	}
	return res, nil
}

type HorribleItem struct {
	Name     string
	Episode  string
	Torrents map[string]string
}

func (ht *HorribleItem) String() string {
	return fmt.Sprintf("%s - %s at %d locations", ht.Name, ht.Episode, len(ht.Torrents))
}

func parseLatestHorrible(source io.Reader) ([]HorribleItem, error) {
	doc, err := goquery.NewDocumentFromReader(source)
	if err != nil {
		return nil, err
	}
	var res []HorribleItem
	episode_pattern := regexp.MustCompile("\\([0-9/]+\\) (.*) - (\\d+)")
	doc.Find("div.episode").Each(func(i int, s *goquery.Selection) {
		title := nodeText(s)
		episode_match := episode_pattern.FindAllStringSubmatch(title, -1)
		if len(episode_match) == 0 {
			log.Println("Horrible: Could not parse title/episode " + title)
			return
		}
		var hi HorribleItem
		hi.Name = strings.Trim(episode_match[0][1], " ")
		hi.Episode = strings.Trim(episode_match[0][2], " ")
		hi.Torrents = make(map[string]string)
		s.Find("span.resolution-links").Each(func(i int, span *goquery.Selection) {
			id, idfound := span.Attr("id")
			var href string
			hreffound := false
			span.Find("a").Each(func(i int, a *goquery.Selection) {
				if a.Text() == "Torrent" {
					href, hreffound = a.Attr("href")
				}
			})
			if !idfound || !hreffound {
				log.Println("Found no id or href in link for title/episode " + title)
				return
			}
			hi.Torrents[id] = href
		})
		if len(hi.Torrents) == 0 {
			log.Println("Horrible: Found no torrents for title/episode " + title)
			return
		}
		res = append(res, hi)
	})
	if len(res) == 0 {
		return nil, errors.New("No episodes div [div.episode]")
	}
	return res, nil
}

func nodeText(s *goquery.Selection) string {
	var res string
	for child := s.Get(0).FirstChild; child != nil; child = child.NextSibling {
		if child.Type == html.TextNode {
			res = res + child.Data
		}
	}
	return res
}
