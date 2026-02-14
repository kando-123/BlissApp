package pl.polsl.blissapp.common;

public interface Callback<S, F>
{
    void onSuccess(S data);
    void onFailure(F data);
}
