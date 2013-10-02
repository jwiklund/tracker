package source

import (
	"os"
	"testing"
)

func TestParseXKCD(t *testing.T) {
	file, err := os.Open("test/xkcd.com")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseSimpleItems(file, xkcdUrl(), xkcdPatterns())
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	t.Logf("Result %v", items)
	if items[0].Title != "Look to my coming on the fifth day. At dawn, look to the east. And look to the west to see our shadows!" {
		t.Fatal("Wrong example title")
	}
	if items[0].Link != "http://imgs.xkcd.com/comics/shadowfacts.png" {
		t.Fatal("Wrong example link")
	}
}
