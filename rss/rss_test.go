package rss

import (
	"bytes"
	"github.com/jwiklund/tracker/source"
	"testing"
)

func TestWriteRss(t *testing.T) {
	items := []source.Item{
		source.Item{"Title", "Link", "GUID", "10 Nov 09 00:00", "Hello Content"},
	}
	source := source.NewStatic("Title", "Url", items)
	var b bytes.Buffer
	err := Write(source, &b)
	t.Log(string(b.Bytes()))
	if err != nil {
		t.Error(err)
	}
}
