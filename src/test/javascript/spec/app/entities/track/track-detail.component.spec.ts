/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CatalogTestModule } from '../../../test.module';
import { TrackDetailComponent } from 'app/entities/track/track-detail.component';
import { Track } from 'app/shared/model/track.model';

describe('Component Tests', () => {
    describe('Track Management Detail Component', () => {
        let comp: TrackDetailComponent;
        let fixture: ComponentFixture<TrackDetailComponent>;
        const route = ({ data: of({ track: new Track(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CatalogTestModule],
                declarations: [TrackDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(TrackDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TrackDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.track).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
