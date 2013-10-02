package source

func xkcdPatterns() StringMap {
	return StringMap{
		"curr":  "<img src=\"http://imgs.xkcd.com/comics/([^\"]+).png\" title=\"[^\"]+\" alt=\"[^\"]+\"",
		"title": "<img src=\"http://imgs.xkcd.com/comics/[^\"]+.png\" title=\"([^\"]+)\" alt=\"[^\"]+\"",
	}
}

func xkcdUrl() string {
	return "http://imgs.xkcd.com/comics/%s.png"
}

func NewXKCD() Source {
	return NewSimple("XKCD", "http://xkcd.com/", xkcdUrl(), xkcdPatterns())
}
