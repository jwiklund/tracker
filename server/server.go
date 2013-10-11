package server

import (
	"fmt"
	"github.com/emicklei/go-restful"
	"github.com/jwiklund/tracker/rss"
	"github.com/jwiklund/tracker/source"
	"log"
	"net/http"
	"strings"
)

func Tracker(hs *source.HorribleSource) *restful.WebService {
	ws := new(restful.WebService)
	ws.Path("/").Produces(restful.MIME_XML)
	ws.Route(ws.GET("/questionable").To(handler(source.NewQuestionable())))
	ws.Route(ws.GET("/sinfest").To(handler(source.NewSinfest())))
	ws.Route(ws.GET("/xkcd").To(handler(source.NewXKCD())))
	ws.Route(ws.GET("/horrible").To(handler(source.NewHorrible(hs, ""))))
	ws.Route(ws.GET("/horrible/{serie}").Param(ws.PathParameter("serie", "serie")).To(horribleHandler(hs)))
	ws.Route(ws.GET("/dailyshow").To(handler(source.NewDailyShow())))
	return ws
}

func horribleHandler(hs *source.HorribleSource) func(*restful.Request, *restful.Response) {
	f := func(request *restful.Request, response *restful.Response) {
		ser := strings.Replace(request.PathParameter("serie"), "+", " ", -1)
		handler(source.NewHorrible(hs, ser))(request, response)
	}
	return f
}

func handler(s source.Source) func(*restful.Request, *restful.Response) {
	f := func(request *restful.Request, response *restful.Response) {
		response.Write([]byte("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>"))
		response.Write([]byte("<rss xmlns:atom=\"http://www.w3.org/2005/Atom\" version=\"2.0\">"))
		err := rss.Write(s, response)
		response.Write([]byte("</rss>"))
		if err != nil {
			log.Printf("Error while handling %s: %s", s.Title(), err.Error())
		}
	}
	return f
}

func Run(ip, port string) {
	if port == "" {
		port = "8080"
	}
	list := fmt.Sprintf("%s:%s", ip, port)
	hs := source.NewHorribleSource()
	go hs.Start()
	defer hs.Stop()
	restful.Add(Tracker(hs))
	log.Printf("start listening on " + list)
	log.Fatal(http.ListenAndServe(list, nil))
}
