package source

import (
	"code.google.com/p/go.net/html/atom"
	"errors"
	"fmt"
	"io"
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
	top, err := NewHtml(source)
	if err != nil {
		return nil, err
	}
	body, err := top.ChildAtPath(atom.Html, atom.Body)
	if err != nil {
		return nil, err
	}
	var rs []HorribleItem
	for _, episode := range body.Children(atom.Div) {
		item, err := parseLatestHorribleItem(episode)
		if err != nil {
			return nil, errors.New(err.Error() + " parsing " + episode.String())
		}
		rs = append(rs, *item)
	}
	return rs, nil
}

func parseLatestHorribleItem(episode Html) (*HorribleItem, error) {
	episode_pattern := regexp.MustCompile("\\([0-9/]+\\) (.*) - (\\d+)")
	episode_match := episode_pattern.FindAllStringSubmatch(episode.Text(), -1)
	if len(episode_match) == 0 {
		return nil, errors.New("Could not parse title/episode " + episode.Text())
	}
	var res HorribleItem
	res.Name = strings.Trim(episode_match[0][1], " ")
	res.Episode = strings.Trim(episode_match[0][2], " ")
	res.Torrents = make(map[string]string)
	links := episode.FindAll(func(h Html) bool {
		return h.Node.DataAtom == atom.Span && h.Attribute("class") == "resolution-links"
	})
	for _, link := range links {
		format, link, linkType, err := parseLatestHorribleLink(link)
		if err != nil {
			return nil, err
		}
		if linkType == "Torrent" {
			res.Torrents[format] = link
		}
	}
	if len(res.Torrents) == 0 {
		return nil, errors.New("No links found in " + episode.String())
	}
	return &res, nil
}

func parseLatestHorribleLink(link Html) (string, string, string, error) {
	span, err := link.Find(func(h Html) bool {
		if h.Node.DataAtom == atom.Span && h.Attribute("class") == "resolution-links" {
			return true
		}
		return false
	})
	if err != nil {
		return "", "", "", errors.New("Could not find span[@class='resolution-links'] in " + link.String())
	}
	a, err := span.Find(func(h Html) bool {
		return h.Node.DataAtom == atom.A
	})
	if err != nil {
		return "", "", "", errors.New("Could not find link in " + span.String())
	}
	return span.Attribute("id"), a.Attribute("href"), a.Text(), nil
}
