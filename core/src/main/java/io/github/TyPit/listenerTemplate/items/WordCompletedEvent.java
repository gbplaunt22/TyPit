package io.github.TyPit.listenerTemplate.items;

public final class WordCompletedEvent implements ItemEvent {
    public final String word;
    public final int completedWords;
    public final boolean noMissSinceLastWord;

    public WordCompletedEvent(String word, int completedWords, boolean noMissSinceLastWord) {
        this.word = word;
        this.completedWords = completedWords;
        this.noMissSinceLastWord = noMissSinceLastWord;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.WORD_COMPLETED;
    }
}
