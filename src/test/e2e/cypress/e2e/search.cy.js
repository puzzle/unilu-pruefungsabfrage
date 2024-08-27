beforeEach(() => {
    cy.visit("/search");
})

it('should show info message on search page', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Suche nach einer Prüfung...")
    })
})

it('should display header on search page', () => {
    cy.get('header').then((header) => {
        expect(header).to.contain("Uni Luzern Prüfungsabfrage")
    })
})
it('should display label for searchbar', () => {
    cy.get('body').then((body) => {
        expect(body).to.contain("Prüfungsnummer eingeben*:")
    })
});
it('should display input for exam number', () => {
    cy.get('input[type="text"]').should('exist');
});