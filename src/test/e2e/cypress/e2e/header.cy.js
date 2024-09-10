beforeEach(() => {
    cy.visit("/");
})

it('should display the right image', () => {
    cy.get("header").find("#logo")
        .should('have.attr', 'src')
        .and('include', 'UNILU_Schriftzug_Standard_schwarz_DE.png');

});

it('should navigate to /search when link to the Search Page is clicked', () => {
    cy.get('header').find('#search-routing-link').should('exist');
    cy.get('#search-routing-link').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/search');
    cy.get('body').then((body) => {
        expect(body).to.contain("Suche nach einer Prüfung...")
    })
});