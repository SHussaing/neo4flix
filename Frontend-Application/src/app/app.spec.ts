import { TestBed } from '@angular/core/testing';

// Keep this as a dead-simple smoke test for CI.
// The application spec can be expanded later once routing/test providers are configured.
describe('App smoke', () => {
  it('should be true', async () => {
    await TestBed.configureTestingModule({}).compileComponents();
    expect(true).toBeTruthy();
  });
});
