package app.paseico.ui.marketplace;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class marketplaceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public marketplaceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is marketplace fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}