package source

import (
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
