package com.whitewoodcity.core.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.whitewoodcity.core.node.Button;
import com.whitewoodcity.core.node.Node;
import com.whitewoodcity.core.node.Pane;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.script.ScriptEngine;

public class Json {

    @JacksonXmlProperty(isAttribute = true)
    private String href;

    @JacksonXmlText
    private String json;

    public Json(String json) {
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public JsonObject getJsonObject(){
        return new JsonObject(json);
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "Json{" +
                "href='" + href + '\'' +
                ", json='" + json + '\'' +
                '}';
    }

    public Node generateNode(ScriptEngine engine) throws Exception{
        return generateNodeByJsonObject(this.getJsonObject(), engine);
    }

    public Node generateNodeByJsonObject(JsonObject jsonObject, ScriptEngine engine) throws Exception{
        String type = jsonObject.getString("type");
        Node node;

        switch (type==null?"pane":type){
            case "button":
                Button button = new Button();
                Double x = jsonObject.getDouble("x");
                Double y = jsonObject.getDouble("y");
                Double width = jsonObject.getDouble("width");
                Double height = jsonObject.getDouble("height");
                button.setX(x==null?0:x);
                button.setY(y==null?0:y);
                if(width!=null) button.setWidth(width);
                if(height!=null) button.setHeight(height);
                button.setText(jsonObject.getString("text"));
                node = button;
                break;
            default:
                Pane pane = new Pane();
                JsonArray jsonArray = jsonObject.getJsonArray("children");
                if(jsonArray!=null){
                    for(Object jo:jsonArray){
                        Node child = generateNodeByJsonObject((JsonObject)jo, engine);
                        pane.add(child);
                    }
                }
                node = pane;
                break;
        }

        if(jsonObject.getString("id")!=null&&engine!=null){
            node.setId(jsonObject.getString("id"));
            engine.put(jsonObject.getString("id"), node);
        }

        return node;
    }

}
