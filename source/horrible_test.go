package source

import (
	"code.google.com/p/go-html-transform/h5"
	"code.google.com/p/go.net/html/atom"
	"os"
	"testing"
)

func TestParseLatestHorrible(t *testing.T) {
	file, err := os.Open("test/horriblesubs.info_lib_latest.php")
	if err != nil {
		t.Fatal("Could not open example file", err)
	}
	defer file.Close()
	latest, err := parseLatestHorrible(file)
	if err != nil {
		t.Fatal("Could not parse latest horrible", err)
	}
	t.Log(latest[0].String())
	if latest[0].Name != "Magi S2" {
		t.Fatal("Wrong name")
	}
	if latest[0].Episode != "01" {
		t.Fatal("Wrong episode")
	}
	if len(latest[0].Torrents) != 3 {
		t.Fatal("Wrong locations")
	}
	if latest[0].Torrents["480p"] != "http://www.nyaa.eu/?page=download&tid=480180" {
		t.Fatal("Wrong 480p location")
	}
}

func TestParseLatestHorribleLink(t *testing.T) {
	ex := "<span class=\"resolution-links\" id=\"480p\">" +
		"  <span class=\"dl-label\">" +
		"    <i>Download [HorribleSubs] Magi S2 - 01 [480p].mkv</i>" +
		"  </span>" +
		"  <span class=\"ind-link\">" +
		"    <a href=\"http://www.nyaa.eu/?page=download&amp;tid=480180\">Torrent</a>" +
		"  </span>" +
		"</span>"
	tree, err := h5.NewFromString(ex)
	if err != nil {
		t.Fatal("Invalid example html", err)
	}
	span, err := Html{tree.Top()}.ChildAtPath(atom.Html, atom.Body)
	if err != nil {
		t.Fatal("Setup failure", err)
	}
	format, link, linkType, err := parseLatestHorribleLink(span)
	if err != nil {
		t.Fatal("Parse failure", err)
	}
	t.Logf("Format %s and link %s of type %s", format, link, linkType)
	if format != "480p" {
		t.Fatal("Wrong format ")
	}
	if link != "http://www.nyaa.eu/?page=download&tid=480180" {
		t.Fatal("Wrong link")
	}
	if linkType != "Torrent" {
		t.Fatal("Wrong type")
	}
}
