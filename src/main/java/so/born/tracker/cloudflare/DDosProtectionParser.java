package so.born.tracker.cloudflare;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import so.born.tracker.anime.HorribleFetcher;

import com.google.common.annotations.VisibleForTesting;

public class DDosProtectionParser {
    private ScriptEngine engine;
    private boolean sleep;

    public DDosProtectionParser(ScriptEngineManager manager) {
        this(manager, true);
    }

    @VisibleForTesting
    DDosProtectionParser(ScriptEngineManager manager, boolean sleep) {
        this.sleep = sleep;
        this.engine = manager.getEngineByMimeType("application/javascript");
    }

    public String parse(Document dos, String url) {
        Bindings bindings = engine.createBindings();
        URL base;
        try {
            base = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid original url", e);
        }
        init(bindings, base);
        String script = getScript(dos);
        String challenge = getFormPath(dos, "challenge-form");
        Map<String, String> challengeAttr = getFormAttributes(dos, "challenge-form");
        execute(bindings, script);
        challengeAttr = updateAttributes(bindings, challengeAttr);
        return result(base, challenge, challengeAttr);
    }

    private String result(URL base, String challenge, Map<String, String> challengeAttr) {
        StringBuilder sb;
        try {
            sb = new StringBuilder().append(new URL(base, challenge));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid url returned from form", e);
        }
        boolean first = true;
        List<String> keys = new ArrayList<>(challengeAttr.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if (first) {
                sb.append("?");
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(challengeAttr.get(key));

        }
        return sb.toString();
    }

    private Map<String, String> updateAttributes(Bindings bindings,
            Map<String, String> challengeAttr) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : challengeAttr.entrySet()) {
            String tmpl = "if ('%s' in document) { document['%s'].value.toString(); } else { null; }";
            try {
                String jsKey = entry.getKey().replace('-', '_');
                String value = (String) engine.eval(String.format(tmpl, jsKey, jsKey), bindings);
                result.put(entry.getKey(), Optional.ofNullable(value).orElse(entry.getValue()));
            } catch (ScriptException e) {
                throw new RuntimeException("Could not check for value '" + entry.getKey() + "'", e);
            }
        }
        return result;
    }

    private void execute(Bindings bindings, String script) {
        try {
            engine.eval(script, bindings);
        } catch (ScriptException e) {
            throw new RuntimeException("Could not load fetched script", e);
        }
        try {
            if (sleep) {
                Thread.sleep(4000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        }
        try {
            engine.eval("document.start();", bindings);
        } catch (ScriptException e) {
           throw new RuntimeException("Could not run fetched script", e);
        }
    }

    private String getFormPath(Document dos, String formId) {
        Element form = dos.getElementById(formId);
        if (form == null) {
            throw new RuntimeException("Form '" + formId + "' not found");
        }
        if (form.attr("method") != null && !"GET".equalsIgnoreCase(form.attr("method"))) {
            throw new RuntimeException("Non GET form");
        }
        return form.attr("action");
    }

    private Map<String, String> getFormAttributes(Document dos, String formId) {
        Element form = dos.getElementById(formId);
        if (form == null) {
            throw new RuntimeException("Form '" + formId + "' not found");
        }
        Map<String, String> attrs = new HashMap<>();
        for (Element element : form.select("input")) {
            attrs.put(element.attr("name"), element.attr("value") == null ? "" : element.attr("value"));
        }
        return attrs;
    }

    private String getScript(Document dos) {
        Elements scripts = dos.head().select("script");
        if (scripts.size() != 1) {
            throw new RuntimeException("Wrong number of scripts, got " + scripts.size());
        }
        return scripts.get(0).data();
    }

    private void init(Bindings bindings, URL base) {
        try {
            engine.eval(
                    "var window = {};"
                  + "window.addEventListener = function(a, b) { a(); };"
                  + "var href = '" + new URL(base, "/").toExternalForm() + "';"
                  + "var setTimeout = function(fun) { fun(); };", bindings);
            engine.eval(documentJS(), bindings);
        } catch (ScriptException | MalformedURLException e) {
            throw new RuntimeException("Failed to init document emulation", e);
        }
    }

    private InputStreamReader documentJS() {
        String doc = "so/born/tracker/cloudflare/document.js";
        return new InputStreamReader(HorribleFetcher.class.getClassLoader().getResourceAsStream(doc));
    }
}
