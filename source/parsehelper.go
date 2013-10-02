package source

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"regexp"
)

func parseSource(patterns map[string]string, source io.Reader) (map[string][]string, error) {
	res := make(map[string][]string)
	pat := make(map[string]*regexp.Regexp)
	for name, pattern := range patterns {
		res[name] = []string{}
		pat[name] = regexp.MustCompile(pattern)
	}
	scanner := bufio.NewScanner(source)
	for scanner.Scan() {
		for name, pattern := range pat {
			m := pattern.FindStringSubmatch(scanner.Text())
			if len(m) > 0 {
				if len(m) < 2 {
					return nil, errors.New(fmt.Sprintf("Pattern for %s does not contain a returnable group", name))
				}
				res[name] = append(res[name], m[1])
			}
		}
	}
	return res, nil
}
