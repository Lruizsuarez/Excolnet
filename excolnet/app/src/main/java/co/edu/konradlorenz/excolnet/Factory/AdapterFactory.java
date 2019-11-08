package co.edu.konradlorenz.excolnet.Factory;

import co.edu.konradlorenz.excolnet.Adapters.AdapterPrecios;
import co.edu.konradlorenz.excolnet.Adapters.AdapterSearch;
import co.edu.konradlorenz.excolnet.Adapters.ChatAdapter;
import co.edu.konradlorenz.excolnet.Adapters.CommentAdapter;
import co.edu.konradlorenz.excolnet.Adapters.FriendsAdapter;
import co.edu.konradlorenz.excolnet.Adapters.HostAdapter;
import co.edu.konradlorenz.excolnet.Adapters.NocturneLifeAdapter;
import co.edu.konradlorenz.excolnet.Adapters.PublicationAdapter;
import co.edu.konradlorenz.excolnet.Adapters.SiteAdapter;
import co.edu.konradlorenz.excolnet.Adapters.agenciesAdapter;

public class AdapterFactory {


    public static Adapter getAdapter(AdapterEnum adapterEnum){

        switch (adapterEnum){

            case PRECIOS:
                return new AdapterPrecios();
            case BUSQUEDA:
                return new AdapterSearch();
            case AGENCIAS:
                return new agenciesAdapter();
            case CHAT:
                return new ChatAdapter();
            case COMENTARIOS:
                return new CommentAdapter();
            case AMIGOS:
                return new FriendsAdapter();
            case HOST:
                return new HostAdapter();
            case NOCTURNE:
                return new NocturneLifeAdapter();
            case PUBLICACION:
                return new PublicationAdapter();
            case SITIOS:
                return new SiteAdapter();
             default:
                 return null;
        }
    }
}
