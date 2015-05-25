package com.example.calvin.myapplication.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.calvin.myapplication.R;
import com.example.calvin.myapplication.common.base.SuperRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SimpleListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setHasFixedSize(true);
        setContentView(recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        SuperRecyclerAdapter adapter = new SuperRecyclerAdapter(this,generateListOfBook(),R.layout.item_book, BookViewHolder.class);

        recyclerView.setAdapter(adapter);

        /*adapter.setOnItemClickListener(new AbsViewHolderAdapter.OnItemClickListener<Book>() {
            @Override
            public void onItemClick(AbsViewHolderAdapter<Book> parent, View view, Book object,
                    int position) {
                Toast.makeText(view.getContext(),
                        "Click on: " + object.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private List<Book> generateListOfBook() {
        List<Book> listBook = new ArrayList<Book>();
        listBook.add(new Book("Airbus1", "A300","http://img.d7vg.com/avatar/3RD/30000.t50x50.png"));
        listBook.add(new Book("Airbus2", "A310","http://img.d7vg.com/node/the-binding-of-isaac-rebirth.png"));
        listBook.add(new Book("Airbus3", "A310 MRTT","http://img.d7vg.com/node/the-witcher3.png"));
        listBook.add(new Book("Airbus4", "CC-150 Polaris Canadian Armed Forces","http://img.d7vg.com/node/ryu-ga-gotoku0.png"));
        listBook.add(new Book("Airbus5", "A318","http://img.d7vg.com/node/guacamelee-stce.png"));
        listBook.add(new Book("Airbus6", "A319","http://img.d7vg.com/node/bloodborne.png"));
        listBook.add(new Book("Airbus7", "A319CJ","http://img.d7vg.com/node/gta5.png"));
        listBook.add(new Book("Airbus8", "A320","http://img.d7vg.com/node/sao-lost-song.png"));
        listBook.add(new Book("Airbus9", "A321","http://img.d7vg.com/node/murasaki-baby.png"));
        listBook.add(new Book("Airbus10", "A330","http://img.d7vg.com/node/helldivers.png"));
        listBook.add(new Book("Airbus11", "A330 MRTT","http://img.d7vg.com/node/hohokum.png"));
        listBook.add(new Book("Airbus12", "A340","http://img.d7vg.com/node/race-the-sun.png"));
        listBook.add(new Book("Airbus13", "A350","http://img.d7vg.com/node/the-unfinished-swan.png"));
        listBook.add(new Book("Airbus14", "A380","http://img.d7vg.com/node/the-unfinished-swan.png"));
        listBook.add(new Book("Airbus15", "A400M","http://img.d7vg.com/node/killzone-mercenary.png"));
        listBook.add(new Book("Airbus16", "Beluga","http://img.d7vg.com/node/pvz-garden-warfare.png"));
        listBook.add(new Book("Airbus17", "E-Fan","http://img.d7vg.com/node/driveclub.png"));
        listBook.add(new Book("Boeing18", "Model B","http://img.d7vg.com/node/never-alone.png"));
        listBook.add(new Book("Boeing19", "Model C","http://img.d7vg.com/avaself/sai8808.t50x50.png"));
        listBook.add(new Book("Boeing20", "Model 1","http://img.d7vg.com/game/6473.t100x55.png"));
        /*listBook.add(new Book("Boeing", "Model 2"));
        listBook.add(new Book("Boeing", "Model 3"));
        listBook.add(new Book("Boeing", "Model 4"));
        listBook.add(new Book("Boeing", "Model 5"));
        listBook.add(new Book("Boeing", "Model 6"));
        listBook.add(new Book("Boeing", "Model 7"));
        listBook.add(new Book("Boeing", "Model 8"));
        listBook.add(new Book("Boeing", "10"));
        listBook.add(new Book("Boeing", "15"));
        listBook.add(new Book("Boeing", "21"));
        listBook.add(new Book("Boeing", "40"));
        listBook.add(new Book("Boeing", "42"));
        listBook.add(new Book("Boeing", "50"));
        listBook.add(new Book("Boeing", "53"));
        listBook.add(new Book("Boeing", "54"));
        listBook.add(new Book("Boeing", "55"));
        listBook.add(new Book("Boeing", "56"));
        listBook.add(new Book("Boeing", "57"));
        listBook.add(new Book("Boeing", "58"));
        listBook.add(new Book("Boeing", "63"));
        listBook.add(new Book("Boeing", "64"));
        listBook.add(new Book("Boeing", "66"));
        listBook.add(new Book("Boeing", "67"));
        listBook.add(new Book("Boeing", "69"));
        listBook.add(new Book("Boeing", "74"));
        listBook.add(new Book("Boeing", "77"));
        listBook.add(new Book("Boeing", "80"));
        listBook.add(new Book("Boeing", "81"));
        listBook.add(new Book("Boeing", "83"));
        listBook.add(new Book("Boeing", "89"));
        listBook.add(new Book("Boeing", "93"));
        listBook.add(new Book("Boeing", "95"));
        listBook.add(new Book("Boeing", "96"));
        listBook.add(new Book("Boeing", "97"));
        listBook.add(new Book("Boeing", "99"));
        listBook.add(new Book("Boeing", "100"));
        listBook.add(new Book("Boeing", "101"));
        listBook.add(new Book("Boeing", "102"));
        listBook.add(new Book("Boeing", "200"));
        listBook.add(new Book("Boeing", "202"));
        listBook.add(new Book("Boeing", "203"));
        listBook.add(new Book("Boeing", "204"));
        listBook.add(new Book("Boeing", "205"));
        listBook.add(new Book("Boeing", "209"));
        listBook.add(new Book("Boeing", "214"));
        listBook.add(new Book("Boeing", "215"));
        listBook.add(new Book("Boeing", "218"));
        listBook.add(new Book("Boeing", "221"));
        listBook.add(new Book("Boeing", "222"));
        listBook.add(new Book("Boeing", "223"));
        listBook.add(new Book("Boeing", "226"));
        listBook.add(new Book("Boeing", "227"));
        listBook.add(new Book("Boeing", "235"));
        listBook.add(new Book("Boeing", "236"));
        listBook.add(new Book("Boeing", "246"));
        listBook.add(new Book("Boeing", "247"));
        listBook.add(new Book("Boeing", "248"));
        listBook.add(new Book("Boeing", "251"));
        listBook.add(new Book("Boeing", "256"));
        listBook.add(new Book("Boeing", "264"));
        listBook.add(new Book("Boeing", "266"));
        listBook.add(new Book("Boeing", "267"));
        listBook.add(new Book("Boeing", "272"));
        listBook.add(new Book("Boeing", "273"));
        listBook.add(new Book("Boeing", "274"));
        listBook.add(new Book("Boeing", "276"));
        listBook.add(new Book("Boeing", "278"));
        listBook.add(new Book("Boeing", "281"));
        listBook.add(new Book("Boeing", "294"));
        listBook.add(new Book("Boeing", "299"));
        listBook.add(new Book("Boeing", "306"));
        listBook.add(new Book("Boeing", "307 Stratoliner / Stratofreighter / Strato-clipper / C-75"));
        listBook.add(new Book("Boeing", "314 Clipper"));
        listBook.add(new Book("Boeing", "316"));
        listBook.add(new Book("Boeing", "320"));
        listBook.add(new Book("Boeing", "322"));
        listBook.add(new Book("Boeing", "333"));
        listBook.add(new Book("Boeing", "334"));
        listBook.add(new Book("Boeing", "337"));
        listBook.add(new Book("Boeing", "341"));
        listBook.add(new Book("Boeing", "344"));
        listBook.add(new Book("Boeing", "345"));
        listBook.add(new Book("Boeing", "345-2"));
        listBook.add(new Book("Boeing", "367 Stratofreighter"));
        listBook.add(new Book("Boeing", "367-80"));
        listBook.add(new Book("Boeing", "377 Stratocruiser"));
        listBook.add(new Book("Boeing", "400"));
        listBook.add(new Book("Boeing", "424"));
        listBook.add(new Book("Boeing", "432"));
        listBook.add(new Book("Boeing", "448"));
        listBook.add(new Book("Boeing", "450"));
        listBook.add(new Book("Boeing", "451"));
        listBook.add(new Book("Boeing", "452"));
        listBook.add(new Book("Boeing", "464"));
        listBook.add(new Book("Boeing", "473"));
        listBook.add(new Book("Boeing", "474"));
        listBook.add(new Book("Boeing", "479"));
        listBook.add(new Book("Boeing", "701"));
        listBook.add(new Book("Boeing", "707"));
        listBook.add(new Book("Boeing", "717 Stratotanker"));
        listBook.add(new Book("Boeing", "717 (MD-95)"));
        listBook.add(new Book("Boeing", "720"));
        listBook.add(new Book("Boeing", "727"));
        listBook.add(new Book("Boeing", "737"));
        listBook.add(new Book("Boeing", "739"));
        listBook.add(new Book("Boeing", "747"));
        listBook.add(new Book("Boeing", "747-400"));
        listBook.add(new Book("Boeing", "747SP"));
        listBook.add(new Book("Boeing", "747-8"));
        listBook.add(new Book("Boeing", "757"));
        listBook.add(new Book("Boeing", "767"));
        listBook.add(new Book("Boeing", "E-767"));
        listBook.add(new Book("Boeing", "KC-767"));
        listBook.add(new Book("Boeing", "777"));
        listBook.add(new Book("Boeing", "787"));
*/
        return listBook;
    }
}
