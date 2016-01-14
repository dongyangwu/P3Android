
package android.content.papm;

interface IPersonalizationSupportManager {
    boolean logPersonalizationSignals(String signals, int category);
    List<String> getPersonalizationSignals();

    boolean cleanOldPersonalizationSignals();
    void setStatus(boolean status);
    boolean getStatus();

    void setPersonaeProfile(inout String[] personae, inout int[] value);
    int getTopPersonaeValue();
    String getTopPersonae();
    List<String> getPersonaeByDescOrder();
    int[] getPersonaeValueByDescOrder();
    int getUserProbablyAge();

}