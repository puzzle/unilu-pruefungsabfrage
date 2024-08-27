beforeEach(() => {
    cy.visit("/");
})

it('should navigate to /search when the header link is clicked', () => {
    cy.get('header').find('#search-routing-link').should('exist');
    cy.get('#search-routing-link').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/search');
});