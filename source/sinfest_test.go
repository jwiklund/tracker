package source

import (
	"os"
	"testing"
)

func TestParseSinfest(t *testing.T) {
	file, err := os.Open("test/sinfest.net")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseSimpleItems(file, sinfestUrl(), sinfestPatterns())
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	t.Logf("Result %v", items)
	if items[0].Title != "Magazine" {
		t.Fatal("Wrong example title")
	}
	if items[0].Link != "http://sinfest.net/comikaze/comics/2013-10-02.gif" {
		t.Fatal("Wrong example link")
	}
}
