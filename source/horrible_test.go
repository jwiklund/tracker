package source

import (
	"os"
	"testing"
	"time"
)

func TestHorribleParseLatest(t *testing.T) {
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

func TestHorribleCacheFunc(t *testing.T) {
	hs := NewHorribleSource()
	getts := 0
	logGets := func(string) ([]HorribleItem, error) {
		getts = getts + 1
		return []HorribleItem{}, nil
	}
	hs.getter = logGets
	go hs.Start()
	s := NewHorrible(hs, "")
	s.Items()
	if getts != 2 {
		t.Fatalf("Wrong number of getss, no get recorded %d", getts)
	}
	s.Items()
	if getts != 2 {
		t.Fatalf("Wrong number of getts, double gett recorded %d", getts)
	}
	hs.next = time.Now().Add(-time.Second)
	s.Items()
	if getts != 4 {
		t.Fatalf("Wrong number of getss, no get recorded %d", getts)
	}
	hs.Stop()
}
