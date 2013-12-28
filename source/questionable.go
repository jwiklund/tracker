package source

func questionablePatterns() StringMap {
	return StringMap{
		"curr": "<img src=\"http://www.questionablecontent.net/comics/(\\d+).png\">",
	}
}

func questionableUrl() string {
	return "http://www.questionablecontent.net/comics/%s.png"
}

func NewQuestionable() Source {
	return NewSimple("Questionable Content", "http://questionablecontent.net/", questionableUrl(), questionablePatterns())
}
