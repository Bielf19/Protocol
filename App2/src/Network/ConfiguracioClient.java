package Network;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

public class ConfiguracioClient {
    private String IPServer;
    private int portServer;
    private int id_node;


    public String getIP() {
        return IPServer;
    }

    public void setIP(String IPServer) {
        this.IPServer = IPServer;
    }

    public int getPortServer() {
        return portServer;
    }

    public void setPortServer(int portServer) {
        this.portServer = portServer;
    }

    public int getId() {
        return id_node;
    }

    public void setId(int id) {
        this.id_node = id;
    }


    public ConfiguracioClient[] llegeixJsonClient () throws FileNotFoundException {

        ConfiguracioClient[] config;


        //En primer lloc, s'obtindrà el path actual.

        String f = new File("").getAbsolutePath();
        Gson gson = new Gson();

        //En segon lloc, s'obrirà el JSON corresponent.

        JsonReader reader = new JsonReader(new FileReader(f + "/roots/client.json"));
        config = gson.fromJson(reader, ConfiguracioClient[].class);


        return config;

    }
}
