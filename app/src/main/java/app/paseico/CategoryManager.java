package app.paseico;

public class CategoryManager {

    public static int ConvertCategoryToIntDrawable (String category) {
        int iconIndex = 0;
        //This is to test the method while there is no category defined in create new route
        if (category == null) {
            category ="Sin temática";
        }
        ////////

        switch (category) {
            case "Sin temática":
                iconIndex  = R.drawable.notheme_icon;
                break;
            case "Naturaleza":
                iconIndex = R.drawable.nature_icon;
                break;
            case "Museos":
                iconIndex = R.drawable.museoicon;
                break;
            case "Restaurantes":
                iconIndex = R.drawable.restaurant_icon;
                break;
            case "Monumentos":
                iconIndex = R.drawable.monument_icon;
                break;
            case "Bares":
                iconIndex = R.drawable.bar_icon;
                break;
            default:
                iconIndex  = R.drawable.notheme_icon;
        }
        return iconIndex;
    }


}
