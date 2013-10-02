package rss

import (
	"encoding/xml"
	"github.com/jwiklund/tracker/source"
	gorss "github.com/ungerik/go-rss"
	"io"
	"time"
)

func Write(s source.Source, w io.Writer) error {
	c, err := sourceToChannel(s)
	if err != nil {
		return err
	}
	b, err := xml.Marshal(c)
	if err != nil {
		return err
	}
	w.Write(b)
	return nil
}

func sourceToChannel(s source.Source) (gorss.Channel, error) {
	var c gorss.Channel
	c.Title = s.Title()
	c.Link = s.Url()
	c.LastBuildDate = gorss.Date(time.RFC822)
	var items []gorss.Item
	sourceItems, err := s.Items()
	if err != nil {
		return gorss.Channel{}, err
	}
	for _, item := range sourceItems {
		i := gorss.Item{
			Title:       item.Title,
			Link:        item.Link,
			GUID:        item.GUID,
			Description: item.Content,
			PubDate:     gorss.Date(item.Date),
		}
		items = append(items, i)
	}
	c.Item = items
	return c, nil
}
