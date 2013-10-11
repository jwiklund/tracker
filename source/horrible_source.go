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
)

type HorribleItem struct {
	Name     string
	Episode  string
	Torrents map[string]string
}

func (ht *HorribleItem) String() string {
	return fmt.Sprintf("%s - %s at %d locations", ht.Name, ht.Episode, len(ht.Torrents))
}

func NewHorribleSource() *HorribleSource {
	return &HorribleSource{}
}

type HorribleSource struct{}

func (hs *HorribleSource) Start() {
}

func (hs *HorribleSource) Stop() {
}

func (hs *HorribleSource) Url() string {
	return "http://horriblesubs.info/lib/latest.php"
}

func (hs *HorribleSource) Items() ([]HorribleItem, error) {
	resp, err := http.Get(hs.Url())
	if err != nil {
		msg := fmt.Sprintf("Could not fetch %s due to %s", hs.Url(), err.Error())
		return nil, errors.New(msg)
	}
	defer resp.Body.Close()
	return parseLatestHorrible(resp.Body)
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
		return nil, errors.New("No episode divs [div.episode]")
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
