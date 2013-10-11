package source

import (
	"fmt"
	"time"
)

func NewHorrible(hs *HorribleSource) Source {
	return &horrible{hs}
}

type horrible struct {
	HorribleSource *HorribleSource
}

func (h horrible) Title() string {
	return "Horrible Subs"
}

func (h horrible) Url() string {
	return h.HorribleSource.Url()
}

func (h horrible) Items() ([]Item, error) {
	items, err := h.HorribleSource.Items()
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
			for _, k := range item.Torrents {
				link = item.Torrents[k]
				quality = k
				break
			}
		}
		it.GUID = link
		it.Link = link
		it.Title = fmt.Sprintf("%s - %s (%s)", item.Name, item.Episode, quality)
		// TODO anidb link
		it.Content = fmt.Sprintf("<a href='%s'>%s</a>", link, it.Title)
		res[i] = it
	}
	return res, nil
}
