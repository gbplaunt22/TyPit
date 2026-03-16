package io.github.TyPit.listenerTemplate.items;

public final class CharacterTypedCorrectEvent implements ItemEvent {
    public final char character;
    public final int completedWords;
    public final int currentCharIndex;

    public CharacterTypedCorrectEvent(char character, int completedWords, int currentCharIndex) {
        this.character = character;
        this.completedWords = completedWords;
        this.currentCharIndex = currentCharIndex;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.CHARACTER_TYPED_CORRECT;
    }
}
