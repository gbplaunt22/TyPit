package io.github.TyPit.listenerTemplate.items;

public final class CharacterTypedWrongEvent implements ItemEvent {
    public final char typedCharacter;
    public final char expectedCharacter;
    public final int completedWords;
    public final int currentCharIndex;

    public CharacterTypedWrongEvent(char typedCharacter, char expectedCharacter, int completedWords, int currentCharIndex) {
        this.typedCharacter = typedCharacter;
        this.expectedCharacter = expectedCharacter;
        this.completedWords = completedWords;
        this.currentCharIndex = currentCharIndex;
    }

    @Override
    public ItemEventType type() {
        return ItemEventType.CHARACTER_TYPED_WRONG;
    }
}
