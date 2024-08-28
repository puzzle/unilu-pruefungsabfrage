beforeEach(() => {
    cy.visit("/");
})

it('should navigate to /search when link to the Search Page is clicked', () => {
    cy.get('header').find('#search-routing-link').should('exist');
    cy.get('#search-routing-link').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/search');
    cy.get('body').then((body) => {
        expect(body).to.contain("Suche nach einer Prüfung...")
    })
});