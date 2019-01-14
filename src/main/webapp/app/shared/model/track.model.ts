export interface ITrack {
    id?: number;
    name?: string;
    bpm?: number;
}

export class Track implements ITrack {
    constructor(public id?: number, public name?: string, public bpm?: number) {}
}
