package source

func questionablePatterns() StringMap {
	return StringMap{
		"curr": "<img id=\"strip\" src=\"http://www.questionablecontent.net/comics/(\\d+).png\">",
		"prev": "<li><a href=\"view.php\\?comic=(\\d+)\">Previous</a></li>",
	}
}

func questionableUrl() string {
	return "http://www.questionablecontent.net/comics/%s.png"
}

func NewQuestionable() Source {
	return NewSimple("Questionable Content", "http://questionablecontent.net/", questionableUrl(), questionablePatterns())
}
