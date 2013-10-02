package source

type Source interface {
	Items() ([]string, error)
}
