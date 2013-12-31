package source

import (
	"os"
	"strings"
	"testing"
)

func TestParseDailyShow(t *testing.T) {
	file, err := os.Open("test/dailyshow.com")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	items, err := parseDailyShow(file)
	if err != nil {
		t.Fatal("Could not parse example file", err)
	}
	for _, item := range items {
		t.Logf("Title %s, Url %s, Date %s, Content %s", item.Title, item.Link, item.Date, item.Content)
	}
	if !strings.Contains(items[0].Title, "2013-10-09 01:01:01 Michael Fassbender") {
		t.Fatal("Wrong example title")
	}
	if items[0].Link != "http://www.thedailyshow.com/full-episodes/wed-october-9-2013-michael-fassbender" {
		t.Fatal("Wrong example link")
	}
}