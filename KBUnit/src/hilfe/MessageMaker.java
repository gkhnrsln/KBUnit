package hilfe;

/**
 * konstruiert eine Test-Report-Message
 * &copy; 2017 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class MessageMaker 
{
    public static String makeTestRunFinishedMessage(int totalNumberOfSuccesses, int totalNumberOfFailures
        , int totalNumberOfAbortions, int totalNumberOfSkips, long execTimeMs)
    {
        String firstMessagePart = (totalNumberOfSuccesses == 1)
            ? totalNumberOfSuccesses + " Test erfolgreich"
            : totalNumberOfSuccesses + " Tests erfolgreich";
        String secondMessagePart = (totalNumberOfFailures == 1)
            ? totalNumberOfFailures + " Test fehlgeschlagen"
            : totalNumberOfFailures + " Tests fehlgeschlagen";
        String thirdMessagePart = (totalNumberOfAbortions == 1)
            ? totalNumberOfAbortions + " Test abgebrochen"
            : totalNumberOfAbortions + " Tests abgebrochen";
        String fourthMessagePart = (totalNumberOfSkips == 1)
            ? totalNumberOfSkips + " Test übersprungen"
            : totalNumberOfSkips + " Tests übersprungen";
        String fifthMessagePart = execTimeMs + "ms Laufzeit";
        return firstMessagePart + ", " + secondMessagePart + ", " + thirdMessagePart
            + ", " + fourthMessagePart + ", " + fifthMessagePart;
    }

    public static String makeMultiLineTestRunFinishedMessage(int totalNumberOfSuccesses, int totalNumberOfFailures
        , int totalNumberOfAbortions, int totalNumberOfSkips, long execTimeMs)
    {
        String firstMessagePart = (totalNumberOfSuccesses == 1)
            ? totalNumberOfSuccesses + " Test erfolgreich, "
            : totalNumberOfSuccesses + " Tests erfolgreich, ";
        String secondMessagePart = (totalNumberOfFailures == 1)
            ? totalNumberOfFailures + " Test fehlgeschlagen, "
            : totalNumberOfFailures + " Tests fehlgeschlagen, ";
        String thirdMessagePart = (totalNumberOfAbortions == 1)
            ? totalNumberOfAbortions + " Test abgebrochen, "
            : totalNumberOfAbortions + " Tests abgebrochen, ";
        String fourthMessagePart = (totalNumberOfSkips == 1)
            ? totalNumberOfSkips + " Test übersprungen"
            : totalNumberOfSkips + " Tests übersprungen";
        String fifthMessagePart = execTimeMs + "ms Laufzeit";
        return "<html>" + firstMessagePart + "<br>" + secondMessagePart + "<br>" + thirdMessagePart
            + "<br>" + fourthMessagePart + "<br>" + fifthMessagePart + "</html>";
    }

}
