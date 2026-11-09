public record HuffmanLeaf (int freq, char tieBreak) implements HuffmanTree{

    @Override
    public boolean comesBefore(HuffmanTree other) {
        int otherFreq = HuffmanTree.frequency(other);
        int otherAscii = HuffmanTree.ascii(other);

        if(this.freq() != otherFreq){
            return this.freq() < otherFreq;
        } else if(this.freq() == otherFreq){
            return this.tieBreak() < otherAscii;
        }

        return false;
    }

    @Override
    public HuffmanTree combine(HuffmanTree other) {
        int totalFreq = this.freq() + HuffmanTree.frequency(other);
        int totalAscii;
        if(this.tieBreak < HuffmanTree.ascii(other)){
            totalAscii = this.tieBreak;
        } else {
            totalAscii = HuffmanTree.ascii(other);
        }

        if(this.comesBefore(other)){
            return new HuffmanNode(totalAscii, totalFreq, this, other);
        } else if(other.comesBefore(this)){
            return new HuffmanNode(totalAscii, totalFreq, other, this);
        }
        return this;
    }

    @Override
    public int compareTo(HuffmanTree other) {
        if(this.comesBefore(other)){
            return -1;
        } else if(other.comesBefore(this)){
            return 1;
        }
        return 0;
    }
}
