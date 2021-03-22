package com.example.finkino_w9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    Spinner list_theater;
    Context context = null;
    TextView show_showing;
    EditText set_date;
    Button xml_button;
    ListView leffalista;
    ArrayList<Teatteri> teatterit = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        list_theater = (Spinner) findViewById(R.id.spinner);
        show_showing = (TextView) findViewById(R.id.textView);
        set_date = (EditText) findViewById(R.id.editTextTextPersonName);
        xml_button = (Button) findViewById(R.id.buttonRead);
        leffalista = (ListView) findViewById(R.id.layout);

        set_date.addTextChangedListener(new FieldWatcher());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new LoadXML().execute("https://www.finnkino.fi/xml/TheatreAreas/");

        while(true) {
            try {
                Thread.sleep(1000);
                updateSpinner();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private class LoadXML extends AsyncTask<String, Integer, String>{



    @Override
    protected String doInBackground(String... strings) {
        String URL = strings[0];
        readXML(URL);
        return null;
    }
}

    public class FieldWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() != 10) {
                show_showing.setText("Päivämäärän tulee olla muotoa pp.kk.vvvv.\n" + "Esimerkki: 22.03.2021");
            }else{
                show_showing.setText("Valitse teatteri ja näytä esitykset.");
                if(list_theater.getSelectedItem() == null){
                    updateSpinner();

                }
            }
        }
    }

    public void getShows(View v){
        int teatteri = (int) list_theater.getSelectedItemId();
        int area = teatterit.get(teatteri).getId();
        String date = set_date.getText().toString();
        String urlString = "https://www.finnkino.fi/xml/Schedule/?area="+area+"&dt="+date;
        //urlString = "https://www.finnkino.fi/xml/Schedule/?area="+"1016"+"&dt="+"22.03.2021";
        System.out.println(urlString);

        setShows(urlString);


    }


    public void setShows(String urlString){

        try {
            String leffa_nimi;
            ArrayList<String> leffaItems = new ArrayList<>();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getDocumentElement().getElementsByTagName("Show");
            System.out.println(nList.getLength());
        for(int i = 0; i < nList.getLength(); i++){
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println(element.getElementsByTagName("Title").item(0).getTextContent());
                leffa_nimi = element.getElementsByTagName("Title").item(0).getTextContent();
                leffaItems.add(leffa_nimi);
            }
            }

        //}
        updateLeffalist(leffaItems);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void readXML(String urlString) {
            try {
                int ID;
                String name;

                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                //String urlString = "https://www.finnkino.fi/xml/TheatreAreas/";
                Document doc = builder.parse(urlString);
                doc.getDocumentElement().normalize();
                System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getDocumentElement().getElementsByTagName("TheatreArea");

                for (int i = 0; i < nList.getLength(); i++) {
                    Node node = nList.item(i);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        ID = Integer.parseInt(element.getElementsByTagName("ID").item(0).getTextContent());
                        name = element.getElementsByTagName("Name").item(0).getTextContent();
                        teatterit.add(new Teatteri(ID, name));

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } finally {
                System.out.println("-----DONEDED JOB!-----");
            }

        }

    public void updateSpinner() {
        System.out.println("entered");
        List<String> namerList = new ArrayList<>();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, namerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (int i = 0; i < teatterit.size() ; i++){
            namerList.add(teatterit.get(i).getName());

        }
        System.out.println("Uprated");
        list_theater.setAdapter(dataAdapter);

    }

    public void updateLeffalist(ArrayList leffaItems){
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, leffaItems);
        leffalista.setAdapter(adapter);



    }
}