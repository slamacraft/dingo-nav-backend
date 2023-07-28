import {HydratedDocument, Model} from 'mongoose'

export type TBase = {
    createTime: Date
    updateTime: Date
    isDeleted: Boolean
}

export const IBase = {
    createTime: {
        type: Date,
        default: Date.now,
    },
    updateTime: {
        type: Date,
        default: Date.now,
    },
    isDeleted: {
        type: Boolean,
        default: false
    },
}

export interface LogicDelete<T, Method> extends Model<T, {}, Method> {
    logicDeleteById(id: string): Promise<HydratedDocument<T, Method>>
}

export const schemaConfig = {
    timestamps: {
        createdAt: 'createTime',
        updatedAt: 'updateTime',
    },
    statics: {
        logicDeleteById<T, Method>(id: string): Promise<HydratedDocument<T, Method>> {
            return this.findByIdAndUpdate(id, {
                $set: {
                    isDeleted: true
                }
            })
        }
    }
}
