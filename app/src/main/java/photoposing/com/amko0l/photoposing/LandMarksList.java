package photoposing.com.amko0l.photoposing;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amko0l on 4/26/2017.
 */

public class LandMarksList extends ArrayAdapter<LandMarks> {
    private Activity context;
    private List<LandMarks> landMarksList;

    public LandMarksList(Activity context, List<LandMarks> landMarksList){
        super(context, R.layout.vertical_menu, landMarksList);
        this.context = context;
        this.landMarksList = landMarksList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View lisviewitem = inflater.inflate(R.layout.vertical_menu, null, true);
        TextView textViewlat = (TextView) lisviewitem.findViewById(R.id.txtviewlat);
        TextView textViewlon = (TextView) lisviewitem.findViewById(R.id.txtviewlon);

        LandMarks landmark = landMarksList.get(position);
        textViewlat.setText(landmark.getLatitude());
        textViewlon.setText(landmark.getLongitute());

        return lisviewitem;
    }
}
