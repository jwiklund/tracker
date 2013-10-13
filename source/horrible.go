package source

import (
	"fmt"
	"strings"
	"time"
)

func NewHorrible(hs *HorribleSource, filter string) Source {
	return &horrible{hs, filter}
}

type horrible struct {
	HorribleSource *HorribleSource
	Filter         string
}

func (h horrible) Title() string {
	return "Horrible Subs " + h.Filter
}

func (h horrible) Url() string {
	return h.HorribleSource.Url()
}

func (h horrible) Items() ([]Item, error) {
	items, err := h.HorribleSource.Items(h.Filter)
	if err != nil {
		return nil, err
	}
	res := make([]Item, len(items))
	for i, item := range items {
		var it Item
		// TODO parse and store
		it.Date = time.Now().Format("2006-01-02") + " 01:01:01"
		quality := "720p"
		link := item.Torrents[quality]
		if link == "" {
			for k, v := range item.Torrents {
				link = v
				quality = k
				break
			}
		}
		it.GUID = link
		it.Link = link
		it.Title = fmt.Sprintf("%s - %s", item.Name, item.Episode)
		// TODO anidb link
		it.Content = fmt.Sprintf("<a href='%s'>%s (%s)</a>", link, it.Title, quality)
		var other []string
		for k, v := range item.Torrents {
			if k != quality {
				other = append(other, fmt.Sprintf("<a href='%s'>%s</a>", v, k))
			}
		}
		if len(other) > 0 {
			it.Content = it.Content + " (" + strings.Join(other, " | ") + ")"
		}
		res[i] = it
	}
	return res, nil
}
