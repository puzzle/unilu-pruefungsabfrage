beforeEach(() => {
    cy.visit("/");
})

it('should display the right image', () => {
    cy.get("footer").find("img")
        .should('have.attr', 'src')
        .and('include', 'swissuniversities.png');
})

it('should redirect to swissuniversities', () => {
    cy.get("footer").find("a").eq(0)
        .should('have.attr', 'href')
        .and('include', 'https://www.swissuniversities.ch/');
});

it('should redirect to impressum', () => {
    cy.get("footer").find("a").eq(1)
        .should('have.attr', 'href')
        .and('include', 'https://www.unilu.ch/impressum/');
});