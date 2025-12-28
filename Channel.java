public class Channel {


    public double errorProbability = 0f;

    public Channel(double errorProbability) {
        this.errorProbability = errorProbability;
    }

    /// Atsitiktinai apverčia gautos žinutės bitus
    /// message - siunčiama žinutė
    /// Gražina:
    /// Naują bitų vektorių, kuris yra toks pats kaip message, bet su atsitiktinai apverstais bitais
    public BitVector send(BitVector message) {
        BitVector sentMessage = new BitVector(message.length());

        for(int i = 0; i < message.length(); i++) {
            if(Math.random() < errorProbability) {
                sentMessage.set(i, !message.get(i));
            }
            else {
                sentMessage.set(i, message.get(i));
            }
        }

        return sentMessage;
    }
}

