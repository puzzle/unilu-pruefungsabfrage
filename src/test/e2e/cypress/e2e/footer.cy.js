beforeEach(() => {
    cy.visit("/");
})

it('should display the right image', () => {
    cy.get('[data-testid="swissuniversities-logo"]')
        .should('have.attr', 'src')
        .and('include', 'swissuniversities.png');
})

it('should redirect to swissuniversities', () => {
    cy.get('[data-testid="swissuniversities-link"]')
        .should('have.attr', 'href')
        .and('include', 'https://www.swissuniversities.ch/');
});

it('should redirect to impressum', () => {
    cy.get('[data-testid="impressum-link"]')
        .should('have.attr', 'href')
        .and('include', 'https://www.unilu.ch/impressum/');
});