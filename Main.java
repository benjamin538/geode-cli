import util.Logging;

public class Main {
    public static void main(String[] args) {
        Logging logger = new Logging();
        logger.clearTerminal();
        logger.warn("AAAAA");
        logger.info("AAAAA");
        Boolean result = logger.confirm("stop program?", false);
        if (result) {
            logger.done("AAAAA");
            return;
        }
        logger.askValue("provide something then", "something", true);
        logger.askValue("now provide something ", "something", false);
        logger.fail("AAAAA");
        logger.fatal("fatal AAAAA");
    }
}
