beforeEach(() => {
    cy.visit("/search");
})

it('should show info message on search page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Suche nach einer Prüfung...")
    })
})

it('should display header on search page', () => {
    cy.get('img').should("be.visible");
})

it('should display label for searchbar', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Prüfungsnummer eingeben*:")
    })
});

it('should display input for exam number', () => {
    cy.get('input[type="text"]').should('exist');
});

it('should display results when searching with valid exam number', () => {
    cy.get('input[type="text"]').type('11000');
    cy.get('button').click();
    cy.contains('Privatrecht');
});

it('should display error when no exams were found', () => {
    cy.get('input[type="text"]').type('99999');
    cy.get('button').click();
    cy.contains('Keine Prüfungen für die Prüfungslaufnummer 99999 gefunden');
});

it('should display error when exam number is invalid', () => {
    cy.get('input[type="text"]').type('abc');
    cy.get('button').click();
    cy.contains('Prüfungsnummer muss 5 Ziffern lang sein');
});
