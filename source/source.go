package source

type Source interface {
	Title() string
	Url() string
	Items() ([]Item, error)
}

type Item struct {
	Title   string
	Link    string
	GUID    string
	Date    string
	Content string
}

func NewStatic(title, url string, items []Item) Source {
	return &staticSource{title, url, items}
}

type staticSource struct {
	title string
	url   string
	items []Item
}

func (s *staticSource) Title() string {
	return s.title
}

func (s *staticSource) Url() string {
	return s.url
}

func (s *staticSource) Items() ([]Item, error) {
	return s.items, nil
}
