package source

func sinfestPatterns() StringMap {
	return StringMap{
		"curr":  "<img src=\"http://sinfest.net/comikaze/comics/([^\"]+).gif\" alt=\"[^\"]+\" border=\"0\"",
		"title": "<img src=\"http://sinfest.net/comikaze/comics/[^\"]+.gif\" alt=\"([^\"]+)\" border=\"0\"",
	}
}

func sinfestUrl() string {
	return "http://sinfest.net/comikaze/comics/%s.gif"
}

func NewSinfest() Source {
	return NewSimple("XKCD", "http://www.sinfest.net/", sinfestUrl(), sinfestPatterns())
}
